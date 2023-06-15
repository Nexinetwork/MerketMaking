/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.UserRole;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "\"tblUser\"", schema = "\"schUser\"")
public class User implements UserDetails,Serializable {

	private static final long serialVersionUID = 8463125357769272244L;

	private final static Logger logger = LoggerFactory.getLogger(User.class);

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

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"userRole\"")
	private UserRole userRole;

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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		logger.info("Role name is : " + userRole.name());
		return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return emailAddress;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return userStatus.equals(UserStatus.Active);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return userStatus.equals(UserStatus.Active);
	}

}
