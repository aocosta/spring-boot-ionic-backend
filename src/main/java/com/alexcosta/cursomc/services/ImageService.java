package com.alexcosta.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alexcosta.cursomc.services.exceptions.FileException;

@Service
public class ImageService {
	
	public BufferedImage getJpgImageFromFile(MultipartFile uploadedFile) {
		// Obtem a extensão do arquivo
		String ext = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
		
		// Rejeita arquivo que não for png ou jpg
		if (!"png".equals(ext) && !"jpg".equals(ext)) {
			throw new FileException("Somente imagens PNG e JPG são permitidas");
		}
		
		try {
			BufferedImage img = ImageIO.read(uploadedFile.getInputStream());
			
			//Converte arquivo png para jpg
			if ("png".equals(ext)) {
				img = pnjToJpg(img);
			}
			
			return img;
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}

	//Converte arquivo png para jpg
	public BufferedImage pnjToJpg(BufferedImage img) {
		BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_BGR);
		jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
		return jpgImage;
	}
	
	// Transforma uma imagem em um tipo InputStream
	public InputStream getInputStream(BufferedImage img, String extension) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(img, extension, os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}
}
