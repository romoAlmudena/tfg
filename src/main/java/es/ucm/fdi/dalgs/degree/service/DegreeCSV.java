package es.ucm.fdi.dalgs.degree.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import es.ucm.fdi.dalgs.domain.Degree;
import es.ucm.fdi.dalgs.domain.info.DegreeInfo;

public class DegreeCSV {

	@SuppressWarnings("unused")
	public List<Degree> readCSVDegreeToBean(InputStream in, String charsetName,
			CsvPreference csvPreference) throws IOException {
		CsvBeanReader beanReader = null;
		List<Degree> degrees = new ArrayList<Degree>();
		try {
			beanReader = new CsvBeanReader(new InputStreamReader(in,
					Charset.forName(charsetName)), csvPreference);
			// the name mapping provide the basis for bean setters
			final String[] nameMapping = new String[] { "code", "name",
					"description" };
			// just read the header, so that it don't get mapped to User
			// object
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getDegreeProcessors();

			DegreeInfo info;
			while ((info = beanReader.read(DegreeInfo.class, nameMapping,
					processors)) != null) {
				Degree d = new Degree();
				d.setInfo(info);
				degrees.add(d);
			}

		} finally {
			if (beanReader != null) {
				beanReader.close();
			}
		}
		return degrees;
	}

	/* CellProcessors have to correspond to the entity database fields */
	private static CellProcessor[] getDegreeProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // Code
				new NotNull(), // Name
				new NotNull(), // Description
		};
		return processors;
	}
	
	public void downloadCSV(HttpServletResponse response,Collection<Degree> degrees ) throws IOException {

		 String csvFileName = "degrees.csv";
		 
	        response.setContentType("text/csv");
	 
	        // creates mock data
	        String headerKey = "Content-Disposition";
	        String headerValue = String.format("attachment; filename=\"%s\"",
	                csvFileName);
	        response.setHeader(headerKey, headerValue);
	 

	        // uses the Super CSV API to generate CSV data from the model data
	        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
	                CsvPreference.STANDARD_PREFERENCE);
	         
	        String[] header = {"code", "name", "description"};
	 
	        csvWriter.writeHeader(header);
	 
	        for (Degree deg : degrees) {
	            csvWriter.write(deg.getInfo(), header);
	        }
	        csvWriter.close();  
	}

}