

package pe.edu.upsjb.constancias.dao;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;


import pe.edu.upsjb.constancias.dto.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Repository


public class DocumentoDaoImpl extends Dao implements DocumentoDao {

    private static final float FIRMA_ANCHO_POR_DEFECTO = 180f;
    private static final float FIRMA_ALTO_POR_DEFECTO = 70f;


    @Override
    public byte[] enviarConstanciaGratuidad(ConstanciaGratuidadRequest request) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdf);


            agregarPlantillaSiExiste(document, pdf);


            LocalDate fechaActual = LocalDate.now();

            DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                    "d 'de' MMMM 'del' yyyy",
                    new Locale("es", "PE")
            );


            PdfFont fuenteNegrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);


            Text tesista1 = new Text(request.getTesista1().toUpperCase());
            tesista1.setFont(fuenteNegrita);

            Text tesista2 = new Text(request.getTesista2().toUpperCase());
            tesista2.setFont(fuenteNegrita);

            Text direccion = new Text ("DIRECCIÓN GENERAL DE INVESTIGACIÓN Y RESPONSABILIDAD SOCIAL").setFont(fuenteNegrita);
            Text titulo = new Text ("CONSTANCIA DE ASESORÍA GRATUITA DE TESIS");
            Text correlativo = new Text ("CONSTANCIA N° " + request.getTesisCorrelativo()).setUnderline().setFont(fuenteNegrita);
            Text lugar_fecha = new Text (request.getSede() + ", " + fechaActual.format(formato));
            Text tesis_titulo = new Text("''" + request.getTesisTitulo().toUpperCase() + "''");

            Paragraph parrafo = new Paragraph();

            parrafo.add(new Text("Por medio de la presente, la Dirección General de Investigación y Responsabilidad Social deja en constancia que "));

            if (request.getTesista2().isEmpty()) {

                String genero = request.getTesista1Genero().equals("H") ? "el Bachiller " : "la Bachiller ";
                parrafo.add(genero);
                parrafo.add(tesista1);
                parrafo.add(new Text(" ha recibido asesoría gratuita para el desarrollo de la tesis denominada: "));

            } else {

                String genero = request.getTesista1Genero().equals("H") ? "el Bachiller " : "la Bachiller ";
                parrafo.add(genero);
                parrafo.add(tesista1);
                parrafo.add(new Text(" y "));
                genero = request.getTesista2Genero().equals("H") ? "el Bachiller " : "la Bachiller ";
                parrafo.add(genero);
                parrafo.add(tesista2);
                parrafo.add(new Text(" han recibido asesoría gratuita para el desarrollo de la tesis denominada: "));
            }

            Text parrafo_2 = new Text ("Atentamente,");

            String firmaBase = "_____________________________________";
            String firmaDirector = "Mtro. Willian Sanchez Tenorio";
            String firmaCargo = "Director General";
            String firmaArea = "Investigación y Responsabilidad Social (e)";

            Text firma = new Text (firmaBase + "\n\n" + firmaDirector + "\n" + firmaCargo + "\n" + firmaArea);



            document.add(
                    new Paragraph(direccion)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(90)
            );

            document.add(
                    new Paragraph(titulo)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(20)
            );

            document.add(
                    new Paragraph(correlativo)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(20)
            );

            document.add(
                    new Paragraph(lugar_fecha)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setPadding(40)
            );

            document.add(
                    parrafo
                            .setTextAlignment(TextAlignment.JUSTIFIED)
                            .setPaddingLeft(40)
                            .setPaddingRight(40)
            );

            document.add(
                    new Paragraph(tesis_titulo.setFont(fuenteNegrita))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPaddingTop(20)
                            .setPaddingBottom(20)
                            .setPaddingLeft(40)
                            .setPaddingRight(40)
            );

            document.add(
                    new Paragraph(parrafo_2)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPaddingLeft(40)
                            .setPaddingRight(40)
            );

            agregarFirmaSiExiste(document, request);

            document.add(
                    new Paragraph(firma.setFont(fuenteNegrita))
                            .setTextAlignment(TextAlignment.CENTER)
                        .setPaddingTop(0)
            );


            /*

            float[] columnWidths = {160f, 340f};
            Table table = new Table(columnWidths);
            table.setBorder(Border.NO_BORDER);
            table.setWidth(UnitValue.createPercentValue(100));

            table.addCell(new Cell()
                    .add(new Paragraph("Número de Requerimiento"))
                    .setPadding(8)
                    .setPaddingLeft(10)
                    .setFontSize(10)
                    .setBorder(null));


            table.addCell(new Cell()
                    .add(new Paragraph(request.getTesista_1())
                    .setPadding(8)
                    .setPaddingLeft(10)
                    .setFontSize(10)
                    .setBorder(null)));

            document.add(table);
            document.add(new AreaBreak());

             */

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new IllegalStateException("Error al generar el PDF", e);

        }
        
    }

    private String valorSeguro(String valor) {

        return valor == null ? "" : valor;

    }

    private void agregarPlantillaSiExiste(Document document, PdfDocument pdf) {

        ClassPathResource plantilla = resolverPlantilla();

        if (!plantilla.exists()) {
            return;
        }

        try {

            byte[] imagenBytes = plantilla.getInputStream().readAllBytes();
            Image image = new Image(ImageDataFactory.create(imagenBytes));
            float pageWidth = pdf.getDefaultPageSize().getWidth();
            float pageHeight = pdf.getDefaultPageSize().getHeight();

            image.scaleAbsolute(pageWidth, pageHeight);
            image.setFixedPosition(1, 0, 0);

            document.add(image);

        } catch (IOException e) {

            throw new IllegalStateException("Error al cargar la plantilla PNG", e);

        }

    }

    private void agregarFirmaSiExiste(Document document, ConstanciaGratuidadRequest request) {

        ClassPathResource firma = resolverFirma();

        if (!firma.exists()) {
            return;
        }

        try {

            byte[] imagenBytes = firma.getInputStream().readAllBytes();
            Image image = new Image(ImageDataFactory.create(imagenBytes));

            ajustarTamanoFirma(image, request.getFirmaAncho(), request.getFirmaAlto());

            image.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            image.setMarginTop(30);
            image.setMarginBottom(-20);

            document.add(image);

        } catch (IOException e) {

            throw new IllegalStateException("Error al cargar la firma PNG", e);

        }

    }

    private void ajustarTamanoFirma(Image image, Float anchoSolicitado, Float altoSolicitado) {

        float anchoOriginal = image.getImageWidth();
        float altoOriginal = image.getImageHeight();

        float ancho = esTamanoValido(anchoSolicitado) ? anchoSolicitado : FIRMA_ANCHO_POR_DEFECTO;
        float alto = esTamanoValido(altoSolicitado) ? altoSolicitado : FIRMA_ALTO_POR_DEFECTO;

        if (esTamanoValido(anchoSolicitado) && esTamanoValido(altoSolicitado)) {
            image.scaleAbsolute(ancho, alto);
            return;
        }

        if (esTamanoValido(anchoSolicitado)) {
            image.scaleAbsolute(ancho, ancho * (altoOriginal / anchoOriginal));
            return;
        }

        if (esTamanoValido(altoSolicitado)) {
            image.scaleAbsolute(alto * (anchoOriginal / altoOriginal), alto);
            return;
        }

        image.scaleAbsolute(ancho, alto);

    }

    private boolean esTamanoValido(Float tamano) {
        return tamano != null && tamano > 0;
    }

    private ClassPathResource resolverPlantilla() {

        ClassPathResource rutaPrincipal = new ClassPathResource("images/plantilla.png");

        if (rutaPrincipal.exists()) {

            return rutaPrincipal;

        }

        return new ClassPathResource("resources/images/plantilla.png");

    }

    private ClassPathResource resolverFirma() {

        ClassPathResource rutaPrincipal = new ClassPathResource("images/firma2.png");

        if (rutaPrincipal.exists()) {

            return rutaPrincipal;

        }

        return new ClassPathResource("resources/images/firma2.png");

    }

}

