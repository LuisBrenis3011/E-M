package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IPdfGenerationService;
import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.contrato.DetalleContrato;
import com.brenis.em.domain.documento.ContratoDocumento;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import com.brenis.em.domain.proveedor.Proveedor;
import com.brenis.em.domain.repository.ContratoDocumentoRepository;
import com.brenis.em.domain.repository.ContratoRepository;
import com.brenis.em.domain.repository.DetalleContratoRepository;
import com.brenis.em.domain.repository.PlantillaContratoRepository;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import com.brenis.em.infrastructure.pdf.HtmlToPdfConverter;
import com.brenis.em.infrastructure.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class PdfGenerationServiceImpl implements IPdfGenerationService {

    private final ContratoRepository contratoRepository;
    private final DetalleContratoRepository detalleContratoRepository;
    private final PlantillaContratoRepository plantillaRepository;
    private final ContratoDocumentoRepository documentoRepository;
    private final HtmlToPdfConverter htmlToPdfConverter;
    private final FileStorageService fileStorageService;

    public PdfGenerationServiceImpl(ContratoRepository contratoRepository,
                                    DetalleContratoRepository detalleContratoRepository,
                                    PlantillaContratoRepository plantillaRepository,
                                    ContratoDocumentoRepository documentoRepository,
                                    HtmlToPdfConverter htmlToPdfConverter,
                                    FileStorageService fileStorageService) {
        this.contratoRepository = contratoRepository;
        this.detalleContratoRepository = detalleContratoRepository;
        this.plantillaRepository = plantillaRepository;
        this.documentoRepository = documentoRepository;
        this.htmlToPdfConverter = htmlToPdfConverter;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ContratoDocumento generarContrato(Long contratoId, Long generadoPor) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", contratoId));

        PlantillaContrato plantilla = plantillaRepository
                .findByProveedorIdAndEsDefaultTrue(contrato.getProveedor().getId())
                .orElseThrow(() -> new BusinessException(
                        "No hay plantilla por defecto configurada. Configure una plantilla primero."));

        String html = plantilla.getContenidoHtml();
        html = reemplazarPlaceholders(html, contrato);

        byte[] pdfBytes = htmlToPdfConverter.convertToPdf(html);

        String nombreArchivo = "contrato_" + contratoId + ".pdf";
        String urlPdf = fileStorageService.storePdf(pdfBytes, nombreArchivo);

        int version = documentoRepository.findByContratoIdOrderByVersionDesc(contratoId)
                .stream().findFirst()
                .map(d -> d.getVersion() + 1)
                .orElse(1);

        ContratoDocumento documento = ContratoDocumento.builder()
                .contrato(contrato)
                .plantilla(plantilla)
                .contenidoHtml(html)
                .urlPdf(urlPdf)
                .version(version)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        return documentoRepository.save(documento);
    }

    @Override
    public List<ContratoDocumento> findByContrato(Long contratoId) {
        return documentoRepository.findByContratoIdOrderByVersionDesc(contratoId);
    }

    @Override
    public ContratoDocumento findById(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContratoDocumento", id));
    }

    private String reemplazarPlaceholders(String html, Contrato contrato) {
        Proveedor proveedor = contrato.getProveedor();
        var evento = contrato.getEvento();
        var cliente = evento.getCliente();
        var tematica = evento.getTematica();

        List<DetalleContrato> detalles = detalleContratoRepository
                .findByContratoIdOrderByOrden(contrato.getId());

        html = html.replace("{{PROVEEDOR_NOMBRE}}", xml(proveedor.getNombreEmpresa()));
        html = html.replace("{{PROVEEDOR_RUC}}", xml(proveedor.getRuc()));
        html = html.replace("{{PROVEEDOR_GERENTE}}", xml(proveedor.getNombreGerente()));

        html = html.replace("{{CLIENTE_NOMBRE}}", xml(cliente.getNombreCompleto()));
        html = html.replace("{{CLIENTE_DNI}}", xml(cliente.getDni()));
        html = html.replace("{{CLIENTE_TELEFONO}}", xml(cliente.getTelefono()));
        html = html.replace("{{CLIENTE_DIRECCION}}", xml(cliente.getDireccion()));
        html = html.replace("{{CLIENTE_REFERENCIA}}", xml(cliente.getReferencia()));

        html = html.replace("{{EVENTO_TIPO}}", xml(evento.getCategoria().getNombre().toUpperCase()));
        html = html.replace("{{EVENTO_TEMATICA}}", xml(tematica != null ? tematica.getNombre() : null));
        html = html.replace("{{EVENTO_FECHA}}", formatFecha(evento.getFechaEvento().atStartOfDay()));
        html = html.replace("{{EVENTO_HORA_INICIO}}", evento.getHoraInicio().toString());
        html = html.replace("{{EVENTO_HORA_FIN}}",
                evento.getHoraFinEstimada() != null ? evento.getHoraFinEstimada().toString() : "");
        html = html.replace("{{EVENTO_NOMBRE_CUMPLEANERO}}", xml(evento.getNombreCumpleanero()));
        html = html.replace("{{EVENTO_EDAD_CUMPLEANERO}}",
                evento.getEdadCumpleanero() != null ? evento.getEdadCumpleanero() + " años" : "");

        html = html.replace("{{CONTRATO_MONTO_TOTAL}}", fmtMonto(contrato.getMontoTotal()));
        html = html.replace("{{CONTRATO_MOVILIDAD}}", fmtMonto(contrato.getCostoMovilidad()));
        html = html.replace("{{CONTRATO_MONTO_ADELANTO}}", fmtMonto(contrato.getMontoAdelanto()));
        html = html.replace("{{CONTRATO_MONTO_PENDIENTE}}", fmtMonto(contrato.getMontoPendiente()));
        html = html.replace("{{CONTRATO_DURACION}}", xml(contrato.getDuracion()));
        html = html.replace("{{CONTRATO_DETALLE_ITEMS}}", buildTablaItems(detalles, "INCLUYE"));
        html = html.replace("{{CONTRATO_OBSEQUIOS}}", buildTablaItems(detalles, "OBSEQUIO"));
        html = html.replace("{{CONTRATO_ADICIONALES}}", buildTablaItems(detalles, "ADICIONAL"));
        html = html.replace("{{CONTRATO_TERMINOS}}", xml(proveedor.getTerminosCondiciones()));
        html = html.replace("{{FECHA_EMISION}}", formatFecha(LocalDateTime.now()));

        return html;
    }

    private String buildTablaItems(List<DetalleContrato> detalles, String tipoDetalle) {
        List<DetalleContrato> filtrados = detalles.stream()
                .filter(d -> d.getTipoDetalle().equals(tipoDetalle))
                .toList();

        if (filtrados.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (DetalleContrato d : filtrados) {
            sb.append("<tr>");
            sb.append("<td class=\"center-bold\">");
            switch (tipoDetalle) {
                case "OBSEQUIO" -> sb.append("OBSEQUIO");
                case "ADICIONAL" -> sb.append("ADICIONAL");
                default -> sb.append(String.format("%02d", d.getCantidad()));
            }
            sb.append("</td>");
            sb.append("<td>")
                    .append(xml(d.getInventario().getNombre()))
                    .append("</td>");
            sb.append("</tr>\n");
        }

        return sb.toString();
    }

    private String formatFecha(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d 'de' MMMM 'del' yyyy", Locale.of("es", "PE"));
        return fecha.format(formatter).toUpperCase();
    }

    private String fmtMonto(BigDecimal monto) {
        return "S/." + String.format("%.2f", monto);
    }

    private String xml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
