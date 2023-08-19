import matplotlib.lines as mlines
import matplotlib.patches as mpatches
font_size =16

# plot legend properties
high_p=mlines.Line2D([], [], color='tab:blue', marker='s', linestyle='solid', markersize=12, label='High priority')
med_p=mlines.Line2D([], [], color='tab:red', marker='^', linestyle='solid',markersize=12, label='Medium priority')
low_p=mlines.Line2D([], [], color='tab:green', marker='.', linestyle='solid', markersize=16, label='Low priority')

