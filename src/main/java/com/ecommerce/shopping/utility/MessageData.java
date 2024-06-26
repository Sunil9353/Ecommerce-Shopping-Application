package com.ecommerce.shopping.utility;
import java.util.Date;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Component
@NoArgsConstructor
public class MessageData {
	
	private String to ;
    private String  subject;
    private Date sentDate;
    private String text;

}
