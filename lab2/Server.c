#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <stdio.h>
#include <memory.h>
#include <string.h>
#include <errno.h>
int main( int argc, char **argv )
{
      struct sockaddr_in peer;
      INIT() //
      set_address( argv[1], argv[2], &peer, "tcp" );
      SOCKET s = socket( AAF_INET, SOCK_STREAM, 0 );
      if( !isvalidsock( s ) )
              error( 1, errno, "Socket colling error" );

      /* Добавляет флаг "не блокирующий" к флагам сокета (как дескриптора файла)*/
      int flags = fcntl( s, F_GETFL, 0 ) );
      if( flags < 0 )
              error( 1, errno, "Error calling fcntl(F_GETFL)" );
      if( fcntl( s, F_SETFL, flags | O_NONBLOCK ) < 0 )
              error( 1, errno, "Error calling fcntl(F_SETFL)" );

      int rc = connect( s, (struct sockaddr * )&peer, sizeof( peer ) );
      if( rc < 0 && errno != EINPROGRESS )
              error( 1, errno, "Error calling connect" );

      if( rc == 0 ) // вдруг уже не надо ждать
      {
              if( fcntl( s, F_SETFL, flags ) < 0 )
                      error( 1, errno, "Error calling fcntl (flags recovery)");
              client( s, &peer );
              return 0;
      }

      /* Если ждать надо, ждем с помощью select'а*/
      fd_set rdevents;
      fd_set wrevents;
      fs_set exevents;
      FD_ZERO( &rdevents );
      FD_SET( s, &rdevents );
      wrevents = rdevents;
      exevents = rdevents;
      struct timeval tv;
      tv.tv_sec = 5;
      tv.tv_usec = 0;
      rc = select( s + 1, &rdevents, &wrevents, &exevents, &tv );
      if( rc < 0 )
              error( 1, errno, "Error calling select" );
      else if( rc == 0 )
              error( 1, 0, "Connection timeout" );
      else if( isconnected( s, &rdevents, &wrevents, &exevents ) )
      {
              if( fcntl( s, F_SETFL, flags ) < 0 )
                      error( 1, errno, "Error calling fcntl (flags recovery)" )
              client( s, &peer );
      }
      else
              error( 1, errno, "Error calling connect" );
      return 0;
}


int isconnected( SOCKET s, fd_set *rd, fd_set *wr, fd_set *ex){
      WSASetLastError( 0 );
      if( !FD_ISSET( s, rd ) && !FD_ISSET( s, wr ) )
              return 0;
      if( !FD_ISSET( s, ex ) )
              return 0;
      return 1;
}