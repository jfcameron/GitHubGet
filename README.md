Desc:
A library for producing java datamodels from github api calls and a small application demoing it.

Library:
produces classes and collections of classes that model github api v3 json response data.
URL data (e.g: repository.issues) is fetched at user access time (aRepo.getIssues()) and its results are simply presented as more data (for (final Issue issue : aRepo.getIssues()) System.out.println(issue.getTitle());)
Currently not all response types are modelled. Currently not all fields are modelled.

Root:
Given a github account, print data about all non-fork repos (language, topic, issue and issue data), then aggregate and print language and topic data.
