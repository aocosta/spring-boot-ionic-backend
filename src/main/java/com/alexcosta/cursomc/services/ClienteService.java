package com.alexcosta.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alexcosta.cursomc.domain.Cidade;
import com.alexcosta.cursomc.domain.Cliente;
import com.alexcosta.cursomc.domain.Endereco;
import com.alexcosta.cursomc.domain.enums.Perfil;
import com.alexcosta.cursomc.domain.enums.TipoCliente;
import com.alexcosta.cursomc.dto.ClienteDTO;
import com.alexcosta.cursomc.dto.ClienteNewDTO;
import com.alexcosta.cursomc.repositories.ClienteRepository;
import com.alexcosta.cursomc.repositories.EnderecoRepository;
import com.alexcosta.cursomc.security.UserSS;
import com.alexcosta.cursomc.services.exceptions.AuthorizationException;
import com.alexcosta.cursomc.services.exceptions.DataIntegrityException;
import com.alexcosta.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imageService;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;

	@Value("${img.profile.size}")
	private Integer size;

	public Cliente find(Integer id) {
		
		// Obtem o usuário logado
		UserSS user = UserService.authenticated();
		
		// Se o usuário não tem perfil de ADMIN e não é o mesmo que está logado
		if (user == null || !user.hasHole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public Cliente findByEmail(String email) {
		
		// Obtem o usuário logado
		UserSS user = UserService.authenticated();
		
		// Se o usuário não tem perfil de ADMIN e não é o mesmo que está logado
		if (user == null || !user.hasHole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Cliente obj = repository.findByEmail(email);
		if (obj == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado! Usuário: " + user.getUsername() + ", Tipo: " + Cliente.class.getName());
		}
		return obj;
		
	}
	
	public List<Cliente> findAll() {
		List<Cliente> clientes = repository.findAll();
		return clientes;
	}

	@Transactional
	public Cliente insert(Cliente cliente) {
		cliente.setId(null);
		cliente = repository.save(cliente);
		enderecoRepository.saveAll(cliente.getEnderecos());
		return cliente;
	}

	public Cliente update(Cliente updateCliente) {
		Cliente cliente = find(updateCliente.getId());
		updateData(cliente, updateCliente);
		return repository.save(cliente);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repository.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir o cliente porque há pedidos relacionados");
		}
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO clienteDto) {
		return new Cliente(clienteDto.getId(), clienteDto.getNome(), clienteDto.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO clienteDto) {
		Cliente cliente = new Cliente(null, clienteDto.getNome(), clienteDto.getEmail(), clienteDto.getCpfOuCnpj(), TipoCliente.toEnum(clienteDto.getTipo()), passwordEncoder.encode(clienteDto.getSenha()));
		
		Cidade cidade  = new Cidade(clienteDto.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, clienteDto.getLogradouro(), clienteDto.getNumero(), clienteDto.getComplemento(), clienteDto.getBairro(), clienteDto.getCep(), cliente, cidade);
		cliente.setEnderecos(Arrays.asList(endereco));

		List<String> telefones = new ArrayList<>();
		telefones.add(clienteDto.getTelefone1());
		if (clienteDto.getTelefone2() != null) {
			telefones.add(clienteDto.getTelefone2());
		}
		if (clienteDto.getTelefone3() != null) {
			telefones.add(clienteDto.getTelefone3());
		}
		cliente.setTelefones(new HashSet<>(telefones));
		
		return cliente;
	}

	private void updateData(Cliente cliente, Cliente updateCliente) {
		cliente.setNome(updateCliente.getNome());
		cliente.setEmail(updateCliente.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {

		// Obtem o usuário logado
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		/* Provisório para a aula 88
		// Realiza o upload da imagem para o S3
		URI uri = s3Service.uploadFile(multipartFile);
		
		// Grava a uri da imagem na tabela de Cliente
		Optional<Cliente> obj = repository.findById(user.getId());
		Cliente cliente = obj.get();
		cliente.setImageUrl(uri.toString());
		repository.save(cliente);
		
		return uri;
		*/

		// Transforma o arquivo em jpg
		BufferedImage jpgImgage = imageService.getJpgImageFromFile(multipartFile);
		
		// Recorta a imagem para deixa-la quadrada
		jpgImgage = imageService.cropSquare(jpgImgage);
		
		// Redimensiona a imagem
		jpgImgage = imageService.resize(jpgImgage, size);
		
		// Cria o nome padrão do arquivo
		String fileName = prefix + user.getId() + ".jpg";
		
		// Realiza o upload da imagem para o S3
		return s3Service.uploadFile(imageService.getInputStream(jpgImgage, "jpg"), fileName, "image");
	
	}

}
