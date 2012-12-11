# Defines the clouds for Mesh terms and Scival concepts and related methods

class tag_occurrences:
    def __init__(self, tagname, occurrences, weight):
        self.name =  tagname
        self.occurrences = occurrences
        self.weight = weight

class tag:
    def __init__(self, tagname, weight):
        self.name =  tagname
        self.weight = weight

class tagCloud:
    def __init__(self, tagSet, tagCloudName):
        self.tagSet=tagSet
        self.tagcloudName = tagCloudName
        self.ranges = self.getRanges()

    def getTagsWeight(self):
        tagsweight=[]
        for tag in self.tagSet:
            weight = tag.weight
            tagsweight.append(weight)
        print tagsweight
        return tagsweight


    def getRanges(self):
      mincount = min(self.getTagsWeight())
      maxcount = max(self.getTagsWeight())
      distrib = (maxcount - mincount) / 9    ;
      index = mincount
      ranges = []
      while (index <= maxcount):
        range = (index, index + distrib)
        index = index + distrib
        ranges.append(range)
      print
      return ranges


