/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.mongodb;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Transient;
import com.mysema.query.annotations.QueryEntities;
import com.mysema.query.apt.DefaultConfiguration;
import com.mysema.query.apt.Processor;

@SupportedAnnotationTypes({"com.mysema.query.annotations.*","com.google.code.morphia.annotations.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class MongodbAnnotationProcessor extends AbstractProcessor{

    private Class<? extends Annotation> entities, entity, embedded, skip;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Running " + getClass().getSimpleName());
        entities = QueryEntities.class;
        entity = Entity.class;
        embedded = Embedded.class;
        skip = Transient.class;

        DefaultConfiguration configuration = new DefaultConfiguration(
                roundEnv, processingEnv.getOptions(), Collections.<String>emptySet(), 
                entities, entity, null, null, embedded, skip);

        Processor processor = new Processor(processingEnv, roundEnv, configuration);
        processor.process();
        return true;
    }

}