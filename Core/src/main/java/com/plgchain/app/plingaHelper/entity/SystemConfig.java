/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author eae966
 *
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "\"tblSystemConfig\"", schema = "\"schConfig\"")
public class SystemConfig implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8159188002006715329L;

	@Id
	@SequenceGenerator(name = "TBLSYSTEMCONFIG_CONFIGID_GENERATOR", sequenceName = "\"seqSystemConfig\"", schema = "\"schConfig\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLSYSTEMCONFIG_CONFIGID_GENERATOR")
	@Column(name = "\"configId\"")
	private long configId;

	@Column(name = "\"configBlobValue\"")
	private byte[] configBlobValue;

	@Column(name = "\"configBooleanValue\"")
	private Boolean configBooleanValue;

	@Column(name = "\"configClobValue\"")
	private String configClobValue;

	@Column(name = "\"configDateValue\"")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+3:30")
	private LocalDate configDateValue;

	@Column(name = "\"configDoubleValue\"")
	private Double configDoubleValue;

	@Column(name = "\"configIntValue\"")
	private Integer configIntValue;

	@Column(name = "\"configName\"")
	private String configName;

	@Column(name = "\"configStringValue\"")
	private String configStringValue;

	@ColumnTransformer(
            read = "PGP_SYM_DECRYPT(\"configEncryptedTextValue\", '!@MYLoveTeted2020secretLOGINILoveYouTedTed@!')",
            write = "PGP_SYM_ENCRYPT (?, '!@MYLoveTeted2020secretLOGINILoveYouTedTed@!')"
    )
	@Column(name = "\"configEncryptedTextValue\"", columnDefinition = "bytea")
	private String configEncryptedTextValue;

	@Column(name = "\"configTimestampValue\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime configTimestampValue;

	@CreationTimestamp
	@Column(name = "\"creationDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@Column(name = "\"lastModifiedDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastModifiedDate;




}
