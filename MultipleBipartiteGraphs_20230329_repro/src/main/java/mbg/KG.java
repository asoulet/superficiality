package mbg;

import tools.SparqlQuerier;

public enum KG {
	
	WIKIDATA_2015("wikidata-archive/wikidata-20150420-all-BETA.ttl.gz","entity:"),
	WIKIDATA_2016("wikidata-archive/wikidata-20160418-all-BETA.ttl.gz","wd:"),
	WIKIDATA_2017("wikidata-archive/wikidata-20170220-all-BETA.ttl.gz","wd:"),
	WIKIDATA_2018("wikidata-archive/wikidata-20180226-all-BETA.ttl.gz","wd:"),
	WIKIDATA_2019("wikidata-archive/wikidata-20190128-all-BETA.ttl.gz","wd:"),
	WIKIDATA_2020("wikidata-archive/wikidata-20201021-truthy-BETA.nt.bz2","http://www.wikidata.org/"),
	WIKIDATA_2021("wikidata-archive/20210213-latest-truthy.nt.bz2","http://www.wikidata.org/"),
	WIKIDATA("wikidata/","http://www.wikidata.org/"),
	CHEMBL("chembl/"),
	BNF("bnf/","http://data.bnf.fr"),
	;
	
	public String dataset;
	public String prefix;
	
	KG(String dataset, String prefix) {
		this.dataset = dataset;
		this.prefix = prefix;
	}
	
	KG(String dataset) {
		this(dataset, null);
	}
	
	public String getLabel(String p) {
		switch (this) {
		case WIKIDATA_2015:
			String uri = "http://www.wikidata.org/entity/" + p.substring(p.indexOf(":") + 1);
			String label = SparqlQuerier.getLabel(uri, "https://query.wikidata.org/sparql");
			if (label == null || label.length() == 0)
				return p;
			else
				return label;
		default:
			return p;
		}
	}
	
	
}
