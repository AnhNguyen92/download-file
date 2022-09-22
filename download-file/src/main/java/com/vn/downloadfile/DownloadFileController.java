package com.vn.downloadfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
public class DownloadFileController {

	@GetMapping(value = { "/", "/index" })
	public String index() {
		return "index";
	}

	@GetMapping("/download-excel")
	@ResponseBody
	public void download(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, IOException, URISyntaxException {
		DownloadFileController app = new DownloadFileController();
		HSSFWorkbook workbook = new HSSFWorkbook(app.getFileAsStream("test.xls"));
		// TODO: copy code write workbook here

		response.setContentType("text/xls");
		response.setHeader("Content-disposition",
				"attachment;filename=" + "ImportReasonTemp-" + Calendar.getInstance().getTimeInMillis() + ".xls");

		try (OutputStream outputStream = response.getOutputStream()) {
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
			workbook.close();
		} catch (Exception e) {
			// TODO: change to log.error
			System.out.println(e.getMessage());
		}
	}

	@GetMapping("/download-zip-excel")
	@ResponseBody
	public void downloadZipFile(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, IOException, URISyntaxException {
		// TODO: copy code write workbook here

		response.setContentType("application/zip");
		response.setHeader("Content-disposition",
				"attachment;filename=" + "ImportReasonTemp-" + Calendar.getInstance().getTimeInMillis() + ".zip");

		byte[] zip = zipFile();
		try (OutputStream outputStream = response.getOutputStream()) {
			outputStream.write(zip);
			outputStream.flush();
		} catch (Exception e) {
			// TODO: change to log.error
			System.out.println(e.getMessage());
		}
	}

	private byte[] zipFile() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos)) {
			byte[] bytes = new byte[2048];
			String fileName = "test.xls";

			try (InputStream fis = getFileAsStream(fileName); BufferedInputStream bis = new BufferedInputStream(fis)) {
				zos.putNextEntry(new ZipEntry(fileName));
				int bytesRead;
				while ((bytesRead = bis.read(bytes)) != -1) {
					zos.write(bytes, 0, bytesRead);
				}
				zos.closeEntry();
			}

			zos.close();
			return baos.toByteArray();
		}
	}

	private InputStream getFileAsStream(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		}
		return inputStream;
	}

}
