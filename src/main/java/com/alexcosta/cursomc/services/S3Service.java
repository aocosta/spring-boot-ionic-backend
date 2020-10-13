package com.alexcosta.cursomc.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alexcosta.cursomc.services.exceptions.FileException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3Service {

	private Logger LOG = LoggerFactory.getLogger(S3Service.class);

	@Autowired
	private AmazonS3 s3client;

	@Value("${s3.bucket}")
	private String bucketName;

	/* Método provisório para realização de testes antes da criação do endpoint //
	// Realiza upload de arquivo para o Amazon S3
 	public void uploadFile(String localFilePath) {
		try {
			// Pega o arquivo que será subido
			File file = new File(localFilePath);
			
			// Mostar mensagem no console
			LOG.info("Iniciando upload");
			
			// Realiza o upload
			s3client.putObject(new PutObjectRequest(bucketName, "teste", file));
 
			LOG.info("Upload finalizado"); }
		catch (AmazonServiceException e) {
			// Mostra o erro no console
			LOG.info("AmazonServiceException: " + e.getErrorMessage());
			
			// Mostra o código de status do HTTP no console
			LOG.info("Status code: " + e.getErrorCode());
		}
		catch (AmazonClientException e) {
			Mostra o erro no console
			LOG.info("AmazonClientException: " + e.getMessage());
		}
	}
	*/

	// Realiza upload de arquivo para o Amazon S3
	public URI uploadFile(MultipartFile multipartFile) {
		try {
			// Obtem o nome do arquivo que veio na requisição
			String fileName = multipartFile.getOriginalFilename();
			
			// Transforma o arquivo MultipartFile em tipo básico para leitura no java
			InputStream is = multipartFile.getInputStream();
			
			// Obtem o tipo do arquivo que veio na requisição
			String contentType = multipartFile.getContentType();

			return uploadFile(is, fileName, contentType);
		} catch (IOException e) {
			throw new FileException("Erro de IO: " + e.getMessage());
		}
	}

	public URI uploadFile(InputStream is, String fileName, String contentType) {
		try {
			// Mostar mensagem no console
			LOG.info("Iniciando upload");
				
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
				
			// Realiza o upload
			s3client.putObject(bucketName, fileName, is, metadata);
			
			// Mostar mensagem no console
			LOG.info("Upload finalizado");
			
			return s3client.getUrl(bucketName, fileName).toURI();
		} catch (URISyntaxException e) {
			throw new FileException("Erro ao converter URL para URI");
		}
	}

}
