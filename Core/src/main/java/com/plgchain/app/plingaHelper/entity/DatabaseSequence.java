/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import org.springframework.data.annotation.Id;

/**
 *
 */
@Data
@Document(collection = "database_sequences")
public class DatabaseSequence implements Serializable {

	private static final long serialVersionUID = 3284048714296760952L;

	@Id
    private String id;

    private long seq;

    public DatabaseSequence() {}

}
