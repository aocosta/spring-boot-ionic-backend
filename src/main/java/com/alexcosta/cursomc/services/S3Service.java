package com.alexcosta.cursomc.services;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	
	private Logger LOG = LoggerFactory.getLogger(S3Service.class);
	
	@Autowired
	private AmazonS3 s3client;
	
	@Value("${s3.bucket}")
	private String bucketName;
	
	// Realiza upload de arquivo para o Amazon S3
	public void uploadFile(String localFilePath) {
		
		try {
			// Pega o arquivo que será subido
			File file = new File(localFilePath);
			
			// Mostar mensagem no console
			LOG.info("Iniciando upload");
			
			// Realiza o upload
			s3client.putObject(new PutObjectRequest(bucketName, "teste", file));
			
			LOG.info("Upload finalizado");
		}
		catch (AmazonServiceException e) {
			// Mostra o erro no console
			LOG.info("AmazonServiceException: " + e.getErrorMessage());
			
			// Mostra o código de status do HTTP no console
			LOG.info("Status code: " + e.getErrorCode());
		}
		catch (AmazonClientException e) {
			// Mostra o erro no console
			LOG.info("AmazonClientException: " + e.getMessage());
		}
		
	}

}
