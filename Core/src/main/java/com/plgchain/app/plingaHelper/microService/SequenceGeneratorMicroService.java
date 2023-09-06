/**
 *
 */
package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.entity.DatabaseSequence;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 */
@Service
public class SequenceGeneratorMicroService implements Serializable {

	private static final long serialVersionUID = 5733254848481411053L;

	private MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorMicroService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {

        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;

    }

}
