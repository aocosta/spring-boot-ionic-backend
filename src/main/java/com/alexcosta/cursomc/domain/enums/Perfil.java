package com.alexcosta.cursomc.domain.enums;

public enum Perfil {
	
	// A palavra ROLE é exigência do Spring Security
	ADMIN(1, "ROLE_ADMIN"),
	CLIENTE(2, "ROLE_CLIENTE");

	private int cod;
	private String descricao;

	private Perfil(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}

	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static Perfil toEnum(Integer cod) {
		
		if (cod == null) {
			return null;
		}
		
		for (Perfil ep : Perfil.values()) {
			if (cod.equals(ep.getCod())) {
				return ep;
			}
		}
		
		throw new IllegalArgumentException("Id inválido: " + cod);
	}

}
