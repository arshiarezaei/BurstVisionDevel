import copy


class CDFResults:
    def __init__(self, dataset, x_value, cdf):
        self.dataset = dataset
        self.x_value = x_value
        self.cdf = cdf

    def __str__(self):
        return self.dataset+"\t"+self.x_value+"\t"+self.cdf
