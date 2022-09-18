#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#define PORT     8080
#define MAXLINE 1024
#define CLADDR_LEN 100

// void *run(void *vargp){

// }
// Driver code
int main() {
    int sockfd;
    char buffer[MAXLINE];

    char clientAddr[CLADDR_LEN];
    struct sockaddr_in servaddr, cliaddr;
    
    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) {
        perror("socket creation failed");
        exit(EXIT_FAILURE);
    }
    
    memset(&servaddr, 0, sizeof(servaddr));
    memset(&cliaddr, 0, sizeof(cliaddr));
    
    servaddr.sin_family = AF_INET; // IPv4
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(PORT);
    
    if ( bind(sockfd, (const struct sockaddr *)&servaddr,
            sizeof(servaddr)) < 0 )
    {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }
    
    int len, n;

    len = sizeof(cliaddr);
    while(1)
    {

       n = recvfrom(sockfd, (char *)buffer, MAXLINE,
                   MSG_WAITALL, ( struct sockaddr *) &cliaddr,
                   &len);

           inet_ntop(AF_INET, &(cliaddr.sin_addr), clientAddr, CLADDR_LEN);
        printf("N: %d\n",n);
       printf("Client %s %d",clientAddr,ntohs(cliaddr.sin_port));
       buffer[n] = '\0';
       printf("Client : %s\n", buffer);
       sendto(sockfd, (const char *)buffer, strlen(buffer),
           MSG_CONFIRM, (const struct sockaddr *) &cliaddr,
               len);

       bzero(buffer,strlen(buffer));

    }
    close(sockfd);
    return 0;
}