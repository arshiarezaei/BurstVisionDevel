import math
import sys
import matplotlib.pyplot as plt
from read_CDF_file import read_CDF_file
from cdf_plot_properties import font_size


def cdf_plot_generator(path_of_data,path_to_plot,x_label):
    plt.figure(1)
    data= read_CDF_file(path_of_data)

    x_value= [r.x_value for r in data]
    cdf = [r.cdf  for r in data]
    plt.plot(x_value, cdf, color='tab:blue', linestyle='solid')
    plt.yticks(range(0, 110,10))
    # plt.xticks(range(int(min(x_value)),int(max(x_value)),10))
    dataset_name=data[0].dataset
    plt.xlabel(x_label)
    plt.ylabel('CDF (%)')
    # plt.title('CDF of updated segments #flows=400,lambda=400')
    plt.grid()
    # plt.show()
    # path = path_to_plot.__add__("/").__add__(dataset_name).__add__('cdf_num_bursts.eps')
    plt.savefig(path_to_plot,format='eps')


if __name__ == '__main__':
    path_of_data= str(sys.argv[1])
    path_to_plot= str(sys.argv[2])
    x_label = str(sys.argv[3])
    cdf_plot_generator(path_of_data,path_to_plot,x_label)