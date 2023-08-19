from CDF_results import CDFResults


def read_CDF_file(fpath):
    results=[]
    file = open(fpath, 'r')
    lines = file.readlines()
    lines = lines[1:len(lines)]
    for line in lines:
        params = line.replace("\n","").split("\t\t")
        result = CDFResults(params[0],float(params[1]),float(params[2]))
        results.append(result)
    return results
