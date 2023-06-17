package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Table(name = "\"tblNode\"", schema = "\"schSevice\"")
public class BlockchainNode implements Serializable {

	private static final long serialVersionUID = 2558511015942442055L;

	@Id
	@SequenceGenerator(name = "TBLNODE_NODEID_GENERATOR", sequenceName = "\"seqNodeNodeId\"", schema = "\"schSevice\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLNODE_NODEID_GENERATOR")
	@Column(name = "\"nodeId\"")
	private long nodeId;

	@ManyToOne
	@JoinColumn(name = "\"blockchainId\"")
	private Blockchain blockchain;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"nodeType\"",nullable = false)
	private BlockchainNodeType nodeType;

	@NotBlank(message = "rpcUrl may not be blank")
	@Column(name = "\"rpcUrl\"",nullable = false)
	private String rpcUrl;

	@Column(name = "\"lastBlock\"",nullable = false)
	private BigInteger lastBlock;

	@Column(name = "\"enabled\"",nullable = false)
	private boolean enabled;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastCheck\"")
	private LocalDateTime lastCheck;

	@Column(name = "\"mustCheck\"",nullable = false)
	private boolean mustCheck;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

	@Column(name = "\"serverIp\"",nullable = false)
	private String serverIp;

	@Column(name = "\"sshPort\"",nullable = false)
	private Integer sshPort;

	@Column(name = "\"serviceNeme\"",nullable = false)
	private String serviceNeme;

	@Column(name = "\"validator\"",nullable = false)
	private boolean validator;

	@Transient
	private Long blockchainId;



}
