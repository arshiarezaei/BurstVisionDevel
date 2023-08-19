#!/bin/sh

python3  ../python/cdf_plot_generator.py "../../../results/traffic_based_analysis/part(8)/cdf_interBurstTime.txt" "../../../plots/cdf_inter-burst_time.eps" "Inter-burst Time (ms)"
python3  ../python/cdf_plot_generator.py "../../../results/traffic_based_analysis/part(8)/cdf_avg_packet_size.txt" "../../../plots/cdf_avg_packet_size.eps" "Average Packet Size (bytes)"
python3  ../python/cdf_plot_generator.py "../../../results/traffic_based_analysis/part(8)/cdf_burstRatio.txt" "../../../plots/cdf_burstRatio.eps" "Burst Ratio"
python3  ../python/cdf_plot_generator.py "../../../results/traffic_based_analysis/part(8)/cdf_bursts_duration.txt" "../../../plots/cdf_bursts_duration.eps" "Burst Duration (micro second)"
python3  ../python/cdf_plot_generator.py "../../../results/traffic_based_analysis/part(8)/cdf_num_packets.txt" "../../../plots/cdf_num_packets.eps" "Number of Packets in Bursts"
python3  ../python/cdf_plot_generator.py "../../../results/traffic_based_analysis/part(8)/cdf_traversed_bytes.txt" "../../../plots/cdf_traversed_bytes.eps" "Traversed Bytes"