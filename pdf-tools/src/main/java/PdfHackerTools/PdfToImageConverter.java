package PdfHackerTools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PdfToImageConverter {

	static String t;

	public static void main(String args[]) {

		try {
			t = PdfUtilities.chooseFolder();

			// TODO: create a method which tests if the file is an
			// ok-to-the-end-PDF to work with

			if (t != null) {
				ArrayList<File> files = PdfUtilities.getPaths(new File(t),
						new ArrayList<File>());
				if (files != null) {
					for (int i = 0; i < files.size(); i++) {
						if (!files.get(i).isDirectory() && files.get(i) != null) {
							try {
								System.out.println(files.get(i)
										.getCanonicalPath());
								if (!PdfUtilities.checkPdfSize(files.get(i))) {
									if (PdfUtilities.testFileHeader(files
											.get(i)) == true) {
										PDDocument testfile = PDDocument
												.load(files.get(i));
										if (!testfile.isEncrypted()) {
											
											List<PDPage> pages = testfile.getDocumentCatalog().getAllPages();
											for (PDPage page : pages) {
											    BufferedImage img = page.convertToImage(BufferedImage.TYPE_INT_RGB, 72);
											    ImageIO.write(img, "jpg", new File(files.get(i) + ".jpg"));
											    i++;
											}											
										}
									}
								}
							}

							catch (IOException e) {
								System.out.println(e);
							}

						}
					}
				}
			}
		}

		catch (IOException e) {
			System.out.println(e);
		}

	}
}
