sudo tcpdump -c 1000 -w mypackets.pcap

echo "Count of the IP packets are -"
sudo tcpdump -n -r mypackets.pcap ip |wc -l

echo "Count of the TCP packets are -"
sudo tcpdump -n -r mypackets.pcap tcp |wc -l

echo "Count of the UDP packets are -"
sudo tcpdump -n -r mypackets.pcap udp |wc -l

echo "Count of the ARP packets are -"
sudo tcpdump -n -r mypacketsc.pcap arp |wc -l

echo "Size of the minimum sized packet -"
sudo tshark -T fields -e frame.len -r mypackets.pcap \
| awk 'BEGIN{min=15000;}{if ($1<0+min) min=$1;} END{print min;}'

echo "Size of the maximum sized packet -"
sudo tshark -T fields -e frame.len -r mypackets.pcap \
| awk 'BEGIN{max=0}{if($1>0+max) max=$1} END {print max}'
