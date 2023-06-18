/**
 *
 */
package com.plgchain.app.plingaHelper.entity.category;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@Table(name = "\"tblCoingeckoCategory\"", schema = "\"schCoingecko\"")
public class CoingeckoCategory implements Serializable {

	private static final long serialVersionUID = 4190258887646914050L;

	@Id
	@NotBlank(message = "category_id should not be null")
	@Column(name = "\"category_id\"",nullable = false,unique = true)
	private String category_id;

	@NotBlank(message = "category_id should not be null")
	@Column(name = "\"name\"",nullable = false,unique = true)
	private String name;

}
