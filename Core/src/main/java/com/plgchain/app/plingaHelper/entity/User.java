/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 */
@Entity
@Data
@Table(name = "\"tblUser\"", schema = "\"schUser\"")
public class User implements Serializable {

	private static final long serialVersionUID = 8463125357769272244L;

	@Id
	@SequenceGenerator(name = "TBLUSER_USERID_GENERATOR", sequenceName = "\"seqUserUserId\"", schema = "\"schUser\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLUSER_USERID_GENERATOR")
	@Column(name = "\"userId\"")
	private long userId;

	@NotBlank
	@Column(name = "\"emailAddress\"",nullable = false,unique = true)
	private String emailAddress;

	@NotBlank
	@Column(name = "\"password\"",nullable = false)
	private String password;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"userStatus\"")
	private UserStatus userStatus;

	@NotBlank
	@Column(name = "\"firstname\"",nullable = false)
	private String firstname;

	@NotBlank
	@Column(name = "\"lastname\"",nullable = false)
	private String lastname;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+3:30")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

}
