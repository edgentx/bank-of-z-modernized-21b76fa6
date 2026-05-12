package com.example.infrastructure.mongo.screenmap;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoScreenMapRepository implements ScreenMapRepository {

  private final ScreenMapMongoDataRepository data;

  public MongoScreenMapRepository(ScreenMapMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<ScreenMapAggregate> findById(String screenMapId) {
    return data.findById(screenMapId).map(this::toAggregate);
  }

  @Override
  public void save(ScreenMapAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  ScreenMapDocument toDocument(ScreenMapAggregate agg) {
    ScreenMapDocument doc = new ScreenMapDocument();
    doc.setId(agg.id());
    doc.setMandatoryFieldsValidated(agg.isMandatoryFieldsValidated());
    doc.setBmsFieldLengthCompliant(agg.isBmsFieldLengthCompliant());
    doc.setVersion(agg.getVersion());
    return doc;
  }

  ScreenMapAggregate toAggregate(ScreenMapDocument doc) {
    ScreenMapAggregate agg = new ScreenMapAggregate(doc.getId());
    agg.setMandatoryFieldsValidated(doc.isMandatoryFieldsValidated());
    agg.setBmsFieldLengthCompliant(doc.isBmsFieldLengthCompliant());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
