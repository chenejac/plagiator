package com.ftn.plagiator.elasticsearch.handler;

import java.io.File;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;

public abstract class DocumentHandler {

	public abstract PaperElastic getIndexUnit(File file);

	public abstract String getText(File file);

}
