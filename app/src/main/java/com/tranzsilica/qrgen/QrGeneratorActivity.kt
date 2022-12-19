package com.tranzsilica.qrgen

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_qr_generator.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class QrGeneratorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_generator)

        submitButton.setOnClickListener {
            if(qrInputET.text.isNullOrEmpty()){
                qrInputET.error = "QR input field can't be empty"
                return@setOnClickListener
            }

            qrCodeGenerator()
        }
    }

    private fun qrCodeGenerator() {
        val multiFormatWriter = MultiFormatWriter()
        val data : String = qrInputET.text.toString()
        try {
            val bitMatrix : BitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            createPDF(bitmap)
        } catch (e : Exception) {
            Toast.makeText(this, "QR code creation failed : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPDF(bitmap: Bitmap) {
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "QRGEN PDF")
        if(!directory.exists()){
            directory.mkdirs()
        }
        val fileID = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date())
        val fileName = "QR_Report_${fileID}.pdf"
        val file = File(directory, fileName)
        val outputStream = FileOutputStream(file) as OutputStream

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument)


        // Paragraph
        val paragraphOne = Paragraph("Contact Details").setBold().setFontSize(30F).setTextAlignment(TextAlignment.CENTER)
        val paragraphTwo = Paragraph(" ").setBold().setFontSize(80F).setTextAlignment(TextAlignment.CENTER)
        paragraphTwo.setMarginTop(60f)

        //
        val columnWidth = floatArrayOf(150F, 900F)
        val table = Table(columnWidth)

        table.addCell(Cell().add(Paragraph("Name : ").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("${nameET.text.toString()}").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("Address : ").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("${addressET.text.toString()}").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("Email : ").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("${emailET.text.toString()}").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("Phone No. : ").setFontSize(15F)).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph("${phoneNoET.text.toString()}").setFontSize(15F)).setBorder(Border.NO_BORDER))


        // QR-Image insert
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bitmapData = stream.toByteArray()
        val imageData = ImageDataFactory.create(bitmapData) as ImageData
        val image = Image(imageData).setWidth(250F).setHorizontalAlignment(HorizontalAlignment.RIGHT)

        document.add(paragraphOne)
        document.add(paragraphTwo)
        document.add(table)
        document.add(paragraphTwo)
        document.add(image)

        document.close()
        Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show()
    }
}