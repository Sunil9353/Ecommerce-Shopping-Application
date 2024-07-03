package com.ecommerce.shopping.securityfilters;

import java.io.IOException;

import com.ecommerce.shopping.utility.ErrorStructure;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

public class TokenExceptionHandler {
	
	public static void  tokenHandler( int status,String message,String rootCause ,HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
	
		response.setStatus(status);
		
		ErrorStructure<String>  errorStructure = new ErrorStructure<String>()
				.setStatus(status)
				.setMessage(message)
				.setRootCause(rootCause);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(),errorStructure);
		
	}

}
