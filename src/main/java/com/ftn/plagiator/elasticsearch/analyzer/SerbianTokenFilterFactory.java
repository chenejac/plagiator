package com.ftn.plagiator.elasticsearch.analyzer;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

import com.ftn.plagiator.elasticsearch.filter.RemoveAccentsFilter;


public class SerbianTokenFilterFactory extends AbstractTokenFilterFactory {

	public SerbianTokenFilterFactory(IndexSettings indexSettings, String name,
			Settings settings) {
		super(indexSettings, name, settings);
	}

	@Override
	public TokenStream create(TokenStream tokenStream) {
		return new RemoveAccentsFilter(tokenStream);
	}

}
