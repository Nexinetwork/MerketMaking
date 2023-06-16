/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.LogAction;
import com.plgchain.app.plingaHelper.constant.LogType;
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
@Table(name = "\"tblLog\"", schema = "\"schLog\"")
public class Log implements Serializable {

	private static final long serialVersionUID = -7395946419564431176L;

	@Id
	@SequenceGenerator(name = "TBLLOG_LOGID_GENERATOR", sequenceName = "\"seqLogLogId\"", schema = "\"schLog\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLLOG_LOGID_GENERATOR")
	@Column(name = "\"logId\"")
	private long logId;

	@NotNull(message = "lobType should not be null")
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"logType\"")
	private LogType logType;

	@NotNull(message = "logAction should not be null")
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"logAction\"")
	private LogAction logAction;

	@NotBlank(message = "logAction should not be empty")
	@Column(name = "\"logDetail\"",nullable = false)
	private String logDetail;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

}
