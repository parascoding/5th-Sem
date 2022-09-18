#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#define PORT   8080
#define MAXLINE 1024

int main() {
    int sockfd;
    char buffer[MAXLINE]; //sending buffer
    char buffer2[MAXLINE];//receiving buffer
    struct sockaddr_in     servaddr;

    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) {
        perror("socket creation failed");
        exit(EXIT_FAILURE);
    }

    memset(&servaddr, 0, sizeof(servaddr));
    
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(PORT);
    servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    
    int n, len;
    while(1)
    {
    printf("enter message");
    fgets(buffer, 100, stdin);
    if (strncmp(buffer,"exit",4)==0)
        break;

    sendto(sockfd, buffer, strlen(buffer),
        MSG_CONFIRM, (const struct sockaddr *) &servaddr,
            sizeof(servaddr)); // Give server address
    
    n = recvfrom(sockfd, (char *)buffer2, MAXLINE,
                MSG_WAITALL, (struct sockaddr *) &servaddr,
                &len); // Give server address
    buffer2[n] = '\0';
    printf("Server : %s\n", buffer);
        }  
    close(sockfd);
    return 0;
}