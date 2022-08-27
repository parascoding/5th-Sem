echo "Please enter password to have super user access"
# sudo su

# tcpdump -c 1000 -w mypackets.pcap

echo "Count of the IP packets are -"
tcpdump -n -r mypackets.pcap ip |wc -l

echo "Count of the TCP packets are -"
tcpdump -n -r mypackets.pcap tcp |wc -l

echo "Count of the UDP packets are -"
tcpdump -n -r mypackets.pcap udp |wc -l

echo "Count of the ARP packets are -"
tcpdump -n -r mypackets.pcap arp |wc -l
