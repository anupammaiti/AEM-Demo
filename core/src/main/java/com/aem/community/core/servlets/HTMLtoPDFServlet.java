package com.aem.community.core.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/downloadpdf", methods = "POST", metatype=true)
@Properties({
        @Property(name = "service.pid", value = "com.aem.community.core.servlets.HTMLtoPDFServlet", propertyPrivate = false),
        @Property(name = "service.vendor", value = "NationalGeographic", propertyPrivate = false) })
public class HTMLtoPDFServlet extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(HTMLtoPDFServlet.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	 
	ResourceResolver resourceResolver;     	     
	
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        
    	String htmlString = "";
		String cssPath = "";
    	
		try {
			if(req.getParameter("htmlString") != null){
				htmlString = req.getParameter("htmlString");
			}
			
			if(req.getParameter("cssPath") != null){
				cssPath = req.getParameter("cssPath");
			}
			
			log.info("htmlstring :: " + htmlString);
			log.info("cssPath :: " + cssPath);
			File pdfFile = createPDFfromHTMLNew(htmlString, cssPath);
			
			if((pdfFile != null) && (pdfFile.isFile()) && (pdfFile.length() != 0)) {
				
				log.info("created a pdf file successfully.. length :: " + pdfFile.length());
				resp.setContentType("application/pdf");	
				resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", pdfFile.getName()));            
	            resp.setContentLength((int)pdfFile.length());
	            
	            ServletOutputStream out = resp.getOutputStream();
	            
	            FileInputStream in = new FileInputStream(pdfFile);
	            byte[] buffer = new byte[4096];
	            int length;
	            while ((length = in.read(buffer)) > 0) {
	            	out.write(buffer, 0, length);
	            }
	            out.close();
	            out.flush();
	            out.close();
	            pdfFile.delete();
			} else {
				resp.getWriter().println("INVALID");
	    		resp.getWriter().println("ERROR MESSAGE : PDF File could not be created");
			}				
		} catch (IOException ie) {
			log.error("IO Exception in HTMLtoPDFServlet doPost Method :: " + ie.getMessage());
			resp.getWriter().println("INVALID");
    		resp.getWriter().println("ERROR MESSAGE : " + ie.getMessage());
		} catch (DocumentException de) {
			log.error("Document Exception in HTMLtoPDFServlet doPost Method :: " + de.getMessage());
			resp.getWriter().println("INVALID");
    		resp.getWriter().println("ERROR MESSAGE : " + de.getMessage());
		} catch (Exception e) {
			log.error("Exception in HTMLtoPDFServlet doPost Method :: " + e.getMessage());
			resp.getWriter().println("INVALID");
    		resp.getWriter().println("ERROR MESSAGE : " + e.getMessage());
		}
    }
    
    public File createPDFfromHTML(String htmlString, String cssString) throws DocumentException, IOException { 	
    	long randomNumber = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
    	log.info("random number :: " + randomNumber);
    	File pdfFile = new File("html-pdf-" + randomNumber);
    	//pdfFile.getParentFile().mkdirs();
        
    	if(pdfFile != null){
    		
    		Document document = new Document();
	        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        log.info("after creating pdf writer object");
	        document.open();
	        
	    	InputStream htmlInputStream = IOUtils.toInputStream(htmlString, "UTF-8");
	        InputStream cssInputStream = IOUtils.toInputStream(cssString, "UTF-8");
	        
	        XMLWorkerHelper.getInstance().parseXHtml(writer, document, htmlInputStream, cssInputStream);
	        log.info("after writing to pdf");
	        document.close();
    		
    		
	        manipulatePdf(randomNumber, pdfFile.getAbsolutePath());
    		
    		/*byte[] image1Data = retrieveContentFromCRXRepository(jcrsession, "/etc/designs/cancer.org/clientlib-screeningtool/css/img/the-defender-fb.jpg");
            Image image1 = Image.getInstance(image1Data);
            image1.setAbsolutePosition(500f, 10f);
            image1.scaleAbsolute(100, 100);
            cb.addImage(image1);*/
    		
    		
	    	/*Document document = new Document();
	        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        log.info("after creating pdf writer object");
	        document.open();
	        
	    	InputStream htmlInputStream = IOUtils.toInputStream(htmlString, "UTF-8");
	        InputStream cssInputStream = IOUtils.toInputStream(cssString, "UTF-8");
	        
	        XMLWorkerHelper.getInstance().parseXHtml(writer, document, htmlInputStream, cssInputStream);
	        log.info("after writing to pdf");
	        document.close();*/
    	}
        
        return pdfFile;
    }
    
    public void manipulatePdf(long randomNumber, String src) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(src));
        Image image = Image.getInstance("/content/dam/geometrixx-instore/cover-images/surfing.jpg");
        PdfImage stream = new PdfImage(image, "", null);
        stream.put(new PdfName(Long.toString(randomNumber)), new PdfName(Long.toString(randomNumber)));
        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
        image.setDirectReference(ref.getIndirectReference());
        image.setAbsolutePosition(36, 400);
        PdfContentByte over = stamper.getOverContent(1);
        over.addImage(image);
        stamper.close();
        reader.close();
    }
    
    public File createPDFfromHTMLNew(String htmlString, String cssString) throws DocumentException, IOException {
    	
    	long randomNumber = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
    	log.info("random number :: " + randomNumber);
    	File pdfFile = new File("html-pdf-" + randomNumber + ".pdf");
    	
    	if(pdfFile != null){
    		log.info("STEP 1");
	    	FontFactory.registerDirectories();
	    	log.info("STEP 2");
	        Document document = new Document();
	        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        log.info("STEP 3");
	        document.open(); 
	        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
	        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
	        log.info("STEP 4");
	        htmlContext.setImageProvider(new AbstractImageProvider() {
	            public String getImageRootPath() {
	                return "/content/dam";
	            }
	        }); 
	        log.info("STEP 5");
	        InputStream htmlInputStream = IOUtils.toInputStream(htmlString, "UTF-8");
	        InputStream cssInputStream = IOUtils.toInputStream(cssString, "UTF-8");
	        
	        CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
	        log.info("STEP 6");
	        Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext, new PdfWriterPipeline(document, writer)));
	        log.info("STEP 7");
	        XMLWorker worker = new XMLWorker(pipeline, true);
	        log.info("STEP 8");
	        XMLParser p = new XMLParser(worker);
	        log.info("STEP 9");
	        p.parse(htmlInputStream);
	        log.info("STEP 10");
	        document.close();    
    	}
    	
    	return pdfFile;
    }
}