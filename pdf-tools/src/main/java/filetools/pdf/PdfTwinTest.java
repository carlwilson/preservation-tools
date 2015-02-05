package filetools.pdf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfTwinTest {

	static String OrgPdf;
	static String MigPdf;
	static long filesizeOrg;
	static long filesizeMig;

	static String folder;

	static PrintWriter outputfile;

	public static void main(String args[]) throws IOException {

		JOptionPane.showMessageDialog(null, "Please choose the folder for the output xml file.", "Enter String Mask", JOptionPane.QUESTION_MESSAGE);
		folder = utilities.BrowserDialogs.chooseFolder();

		if (folder != null) {

			outputfile = new PrintWriter(new FileWriter(folder + "\\PdfTwinTester.xml"));

			String xmlVersion = "xml version='1.0'";
			String xmlEncoding = "encoding='ISO-8859-1'";
			// String xsltStyleSheet =
			// "<?xml-stylesheet type=\"text/xsl\" href=\"PdfTwinTestStyle.xsl\"?>";
			// String xsltLocation = (folder + "//" + "PdfTwinTestStyle.xsl");

			// output.XslStyleSheets.PdfTwinTestXsl();

			outputfile.println("<?" + xmlVersion + " " + xmlEncoding + "?>");
			// outputfile.println(xsltStyleSheet);
			outputfile.println("<PdfTwinTest>");

			OrgPdf = utilities.BrowserDialogs.chooseFile();
			System.out.println(OrgPdf);

			MigPdf = utilities.BrowserDialogs.chooseFile();
			System.out.println(MigPdf);

			outputfile.println("<OriginalFile>" + utilities.fileStringUtilities.getFileName(OrgPdf) + "</OriginalFile>");
			outputfile.println("<MigratedFile>" + utilities.fileStringUtilities.getFileName(MigPdf) + "</MigratedFile>");

			if (OrgPdf != null && MigPdf != null) {

				// the real program starts here, everything else are just
				// prerequisites

				filesizeOrg = OrgPdf.length();
				filesizeMig = MigPdf.length();

				if (filesizeOrg < 16000000 && filesizeMig < 16000000) {

					if (filetools.GenericFileAnalysis.testFileHeaderPdf(OrgPdf) == true && filetools.GenericFileAnalysis.testFileHeaderPdf(MigPdf) == true) {

						PDDocument testfileOrg = PDDocument.load(OrgPdf);
						PDDocument testfileMig = PDDocument.load(MigPdf);

						if (testfileOrg.isEncrypted() || testfileMig.isEncrypted()) {
							JOptionPane.showMessageDialog(null, "One or both of the Pdf-files are encrypted", "Information", JOptionPane.INFORMATION_MESSAGE);
						} else {

							if (PdfAnalysis.checkBrokenPdf(OrgPdf) == false && PdfAnalysis.checkBrokenPdf(MigPdf) == false) {
								String[] linesOrg = PdfAnalysis.extractsPdfLines(OrgPdf);
								String[] linesMig = PdfAnalysis.extractsPdfLines(MigPdf);

								int differences = 0;

								int lenOrg = linesOrg.length;
								int lenMig = linesMig.length;

								outputfile.println("<OrgPDFLinesLength>" + lenOrg + "</OrgPDFLinesLength>");
								outputfile.println("<MigPDFLinesLength>" + lenMig + "</MigPDFLinesLength>");

								if ((lenOrg > lenMig || lenOrg == lenMig)) {

									for (int j = 0; j < lenMig; j++) {

										if (!(linesOrg[j]).equals(linesMig[j])) {								
											outputfile.println("<DifferentLineNumber>" + (j + 1) + "</DifferentLineNumber>");
											outputfile.println("<OriginalLine>" + linesOrg[j] + "</OriginalLine>"); //TODO: cannot display cyrillic stuff
											outputfile.println("<MigrationLine>" + linesMig[j] + "</MigrationLine>");
											
											//TODO: maybe document kind of difference, a space too much, a character false or line completely different
											
											differences++;										
										}
									}
									if (differences == 0) {
										outputfile.println("<PdfTwins>" + "true" + "</PdfTwins>");
									}

									else {
										outputfile.println("<PdfTwins>" + "false" + "</PdfTwins>");
										outputfile.println("<DifferentLines>" + differences + "</DifferentLines>");
									}
								}

								else /* (lenMig > lenOrg) */{
									for (int j = 0; j < lenOrg; j++) {

										// happens twice, maybe create a method?

										if (!(linesOrg[j]).equals(linesMig[j])) {
											outputfile.println("<DifferentLine>");
											outputfile.println("<LineNumber>" + (j + 1) + "</LineNumber>");
											outputfile.println("<OriginalLine>" + linesOrg[j] + "</OriginalLine>");
											outputfile.println("<MigrationLine>" + linesMig[j] + "</MigrationLine>");
											differences++;
											outputfile.println("<(DifferentLine>");
										}
									}
									if (differences == 0) {
										outputfile.println("<PdfTwins>" + "true" + "</PdfTwins>");
									}

									else {
										outputfile.println("<PdfTwins>" + "false" + "</PdfTwins>");
										outputfile.println("<DifferentLines>" + differences + "</DifferentLines>");
									}
								}
							} else {
								System.out.println("Program closed.");
							}
						}
					}

					else {
						System.out.println("One of the files is lacking a PdfHeader.");
						System.out.println("Please choose two proper Pdf-files");
					}

				} else {
					System.out.println("One of the Files or both are too big to be examined:");
					System.out.println(OrgPdf + " Filesize: " + filesizeOrg);
					System.out.println(MigPdf + " Filesize: " + filesizeMig);
				}
			}

			else {
				System.out.println("Please choose two files.");
			}

			outputfile.println("</PdfTwinTest>");
			outputfile.close();
		}
	}
}
