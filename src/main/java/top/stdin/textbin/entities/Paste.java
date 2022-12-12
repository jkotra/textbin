package top.stdin.textbin.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

import lombok.Data;


@Entity
@Data
public class Paste {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	Long date = Instant.now().toEpochMilli();
	
	@Type(type="org.hibernate.type.UUIDCharType")
	@Column(unique = true)
	UUID uuid = UUID.randomUUID();
	
	String title;
	
	@Column(columnDefinition = "MEDIUMTEXT")
	String text;
	
	@Transient
	String captcha;
	
	String deletekey = RandomStringUtils.random(24, true, true);
	
	public Paste remove_delete_key() {
		this.setDeletekey("");
		return this;
	}
	
}
