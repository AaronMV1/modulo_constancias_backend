

package pe.edu.upsjb.constancias.dao;


import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;


import pe.edu.upsjb.constancias.dto.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


@Repository


public class DocumentoDaoImpl extends Dao implements DocumentoDao {


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



            Text direccion = new Text ("DIRECCIÓN GENERAL DE INVESTIGACIÓN Y RESPONSABILIDAD SOCIAL");
            Text titulo = new Text ("CONSTANCIA DE ASESORÍA GRATUITA DE TESIS");
            Text correlativo = new Text ("CONSTANCIA N° " + request.getTesis_correlativo()).setUnderline();
            Text lugar_fecha = new Text (request.getSede() + ", " + fechaActual.format(formato));
            Text tesis_titulo = new Text("''" + request.getTesis_titulo().toUpperCase() + "''");
            Text parrafo = new Text (
                    "Por medio de la presente, la Dirección General de Investigación y Responsabilidad Social deja en constancia que el Bachiller " +
                            request.getTesista_1().toUpperCase() +
                            ", ha recibido asesoría gratuita para el desarrollo de la tesis denominada: "
            );
            Text parrafo_2 = new Text ("Atentamente,");

            String firmaDirector = "Mtro. Willian Sanchez Tenorio";
            String firmaCargo = "Director General";
            String firmaArea = "Investigación y Responsabilidad Social";

            Text firma = new Text (firmaDirector + "\n" + firmaCargo + "\n" + firmaArea);



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
                    new Paragraph(parrafo)
                            .setTextAlignment(TextAlignment.JUSTIFIED)
                            .setPaddingLeft(40)
                            .setPaddingRight(40)
            );

            document.add(
                    new Paragraph(tesis_titulo)
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

            document.add(
                    new Paragraph(firma)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPaddingTop(120)
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

    private ClassPathResource resolverPlantilla() {

        ClassPathResource rutaPrincipal = new ClassPathResource("images/plantilla.png");

        if (rutaPrincipal.exists()) {

            return rutaPrincipal;

        }

        return new ClassPathResource("resources/images/plantilla.png");

    }

}

