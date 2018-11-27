#!/bin/bash
source config.cfg
#mvn clean compile package
mvn exec:java -Dexec.mainClass="ie.tcd.irws.searchengine.SearchEngine"
cd $trec_location
./trec_eval -q $qrels_location $results_location | tee $trec_results_location 
