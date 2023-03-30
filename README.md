# The Structure and Dynamics of Knowledge Graphs, with Superficiality

Large knowledge graphs combine human knowledge garnered from projects ranging from academia and institutions to enterprises and crowdsourcing. Within such graphs, each relationship between two nodes represents a basic fact involving these two entities. The diversity of the semantics of relationships constitutes the richness of knowledge graphs, leading to the emergence of singular topologies, sometimes chaotic in appearance. However, this complex characteristic can be modeled in a simple way by introducing the concept of superficiality, which controls the overlap between relationships whose facts are generated independently. Superficiality also regulates the balance of the global distribution of knowledge by determining the proportion of misdescribed entities. This is the first model for the structure and dynamics of knowledge graphs. It leads to a better understanding of formal knowledge acquisition and organization.

## Table of contents
[[_TOC_]]

## Data

### Real-World KG distributions

Let us recall the preprocessing of the original dumps. We filtered each dump to remove literals and external entities because our model aims at understanding the internal topology of the entity belonging to a given knowledge graph. In the same way that the study of the topology of the Web considers only the pages and their links (i.e., the content of the pages like text and images is ignored). Of course, only the nodes of the knowledge graph corresponding to entities have been kept. Literal values such as dates, strings or images have therefore been removed. Besides, for the BnF and Wikidata knowledge graphs, many relationships link subjects to entities belonging to other external knowledge graphs. For focusing on one graph at a time, we only consider the entities whose Uniform Resource Identifier (URI) is prefixed by `http://data.bnf.fr` or `http://www.wikidata.org/` for the BnF or Wikidata knowledge graph respectively.

We provide here the real-world distributions of the 3 KGs with comma-separated values (CSV) files (the first column for the degree and the second column for the count):
* In-degree distributions: [BnF](data/real/BnF_IN.csv), [ChEMBL](data/real/ChEMBL_IN.csv), [Wikidata](data/real/Wikidata_IN.csv)
* Out-degree distributions: [BnF](data/real/BnF_OUT.csv), [ChEMBL](data/real/ChEMBL_OUT.csv), [Wikidata](data/real/Wikidata_OUT.csv)
These degree distributions are used as ground truth in Figure 1 (see magenta squares).

### Generated KG distributions

We provide an example of generated distributions for the 3 KGs with comma-separated values (CSV) files (the first column for the degree and the second column for the count). Our method is based on a multiplex generative model (denoted by *multiplex*) where each layer has a distinct preferential attachment mechanism parameterized between 0 and 1 (denoted by *parametrized*). We also reports an ablation study using only one layer (denoted by *simplex*) or forcing a linear preferential attachment (denoted by *linear*).

* Results of *muliplex* and *parametrized* exponent  KG (our proposal / used in for green circles in Figure 1):
    * In-degree distributions: [BnF](data/gen/multi_param/BnF_IN.csv), [ChEMBL](data/gen/multi_param/ChEMBL_IN.csv), [Wikidata](data/gen/multi_param/Wikidata_IN.csv)
    * Out-degree distributions: [BnF](data/gen/multi_param/BnF_OUT.csv), [ChEMBL](data/gen/multi_param/ChEMBL_OUT.csv), [Wikidata](data/gen/multi_param/Wikidata_OUT.csv)
* Results of *muliplex* and *linear* exponent  KG:
    * In-degree distributions: [BnF](data/gen/multi_linear/BnF_IN.csv), [ChEMBL](data/gen/multi_linear/ChEMBL_IN.csv), [Wikidata](data/gen/multi_linear/Wikidata_IN.csv)
    * Out-degree distributions: [BnF](data/gen/multi_linear/BnF_OUT.csv), [ChEMBL](data/gen/multi_linear/ChEMBL_OUT.csv), [Wikidata](data/gen/multi_linear/Wikidata_OUT.csv)
* Results of *simplex* and *parametrized* exponent  KG (Bollobas model):
    * In-degree distributions: [BnF](data/gen/simp_param/BnF_IN.csv), [ChEMBL](data/gen/simp_param/ChEMBL_IN.csv), [Wikidata](data/gen/simp_param/Wikidata_IN.csv)
    * Out-degree distributions: [BnF](data/gen/simp_param/BnF_OUT.csv), [ChEMBL](data/gen/simp_param/ChEMBL_OUT.csv), [Wikidata](data/gen/simp_param/Wikidata_OUT.csv)
* Results of *simplex* and *linear* exponent  KG (Barabasi-Albert model):
    * In-degree distributions: [BnF](data/gen/simp_linear/BnF_IN.csv), [ChEMBL](data/gen/simp_linear/ChEMBL_IN.csv), [Wikidata](data/gen/simp_linear/Wikidata_IN.csv)
    * Out-degree distributions: [BnF](data/gen/simp_linear/BnF_OUT.csv), [ChEMBL](data/gen/simp_linear/ChEMBL_OUT.csv), [Wikidata](data/gen/simp_linear/Wikidata_OUT.csv)

## Source code

The entire source code (the computation of the statistics and the generation of the knowledge graphs) is implemented in Java in [`MultipleBipartiteGraphs`](MultipleBipartiteGraphs_20230329_repro/). It was running with JavaSE-1.7 (jre) under Eclipse IDE for Java Developers (includes Incubating components / Version: 2021-03 (4.19.0) / Build id: 20210312-0638) by using a Maven Project. 

### Configuration

* Required libraries (see `pom.xml`):
    * log4j 1.2.17
    * Apache Commons Math3 3.6.1
    * Apache Jeana 3.17.0
    * API for SLF4J 2.0.0-alpha1
* Configuration file: `properties/MBG.properties`:
    * `statistics_path`: path containing the statistics about relationships (kbs files)
    * `data_path`: path containing the data (original KG in RDF format)
* Configuration Java class `mbg.MBG`:
    * `CONFIGURATION_FILE`: configure constant for locating the configuration file 
    * `VERBOSE_MODE`: control the verbosity
    * `FACT_COUNTING`: control what is counting in distribution (`true` = facts; `false` = relationships)
    * `EXPONENT_PARAMETRIZING`: control whether the exponent parametrization (if `false` the exponent of all relationships is 1)
    * `SAMPLE`: enable to do oversampling and undersampling for changing the KG size

### Generate a synthetic KG from a real-world KG

To generate a knowledge graph, the Java class `mbg.KBProfilerTest` must be executed:
1. Choose the knowledge graph by uncommenting the appropriate line
2. Specify the distribution `StatisticsType.In` for in-degree distribution or `StatisticsType.OUT` for out-degree distribution
3. Run the class

Remarks:
* The program seeks to take the appropriate statistics present in the path `statistics_path`. If the statistics do not exist, the program tries to build them from the KG in path `data_path`.
* The parameters of the Java MBG class are taken into account.
* To reproduce Figure 1, it is necessary to make 6 executions by combining the 3 KGs (BnF, CheMBL, Wikidata) an the two connectivities (in-degree, out-degree).

### Simulate a synthtic KG

To simulate a simplified knowledge graph, it is possible to use two Java classes:
`mbg.SynthesisMonoPropertyTest` : It allows to fix all the parameters for generating a synthetic relationship (exponent `ALPHA`, number of facts `COUNT`, attachment probability `BETA`)
`mbg.SynthesisMultiPropertyTest`: It allows to simulate `NUMBER` relationships having the same properties (exponent `ALPHA`, number of facts `COUNT`, attachment probability `BETA`) taking into account a given superficiality `SUPERFICIALITY`

Remarks:
* The parameters of the Java MBG class are taken into account.
* To reproduce Figure 3, it is necessary to make 3 executions: 1. `SynthesisMonoPropertyTest` for plotting the mono-relationship,  `SynthesisMultiPropertyTest` for plotting the 25 relationships with `SUPERFICIALITY=0.05` (facts and number of distinct relationships), and `SynthesisMultiPropertyTest` for plotting the 25 relationships with `SUPERFICIALITY=0.95` (facts and number of distinct relationships).

### Evaluate the parametrization method of the exponent

The parametrization method of the exponent is described in the supplementary materials. To evaluate the quality of our parameterization method, we generated synthetic relationships as ground truth by varying the parameters `ALPHA` and `BETA` between 0 and 1. We then applied our parameterization method for setting `ALPHA` and we compared the distribution from the found parameter with that of the ground truth by means of the Kullback-Leibler divergence. For reproducing this experimentation, it is necessary to execute the Java class `mbg.EstimatedGammaAnalysis`.

Remarks:
* As the parameters of the Java MBG class are taken into account, it is important to disable the verbose mode by setting `VERBOSE=false`.
