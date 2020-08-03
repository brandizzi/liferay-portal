
package com.liferay.portal.search.tuning.gsearch.impl.facet;

import com.liferay.portal.search.tuning.gsearch.api.facet.FacetProcessor;

import org.osgi.service.component.annotations.Component;

/**
 * Default facet processor.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FacetProcessor.class
)
public class DefaultFacetProcessor
	extends BaseFacetProcessor implements FacetProcessor {

	@Override
	public String getName() {
		return NAME;
	}

	public static final String NAME = 
			FacetProcessorFactoryImpl.DEFAULT_FACET_PROCESSOR_NAME;

}