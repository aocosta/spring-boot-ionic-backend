package com.alexcosta.cursomc.resources.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class URL {
	
	// Método para desencodar strings com caracteres especiais do javascript
	public static String decodeParam(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static List<Integer> decodeIntList(String s) {
		/*
		List<Integer> ids = new ArrayList<>();
		String[] vetS = s.split(",");
		for(String nS : vetS) {
			ids.add(Integer.parseInt(nS));
		}
		return ids;
		*/
		
		return Arrays.asList(s.split(",")).stream().map(x -> Integer.parseInt(x)).collect(Collectors.toList());
	}

}
