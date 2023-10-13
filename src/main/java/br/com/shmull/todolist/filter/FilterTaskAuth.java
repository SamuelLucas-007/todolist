package br.com.shmull.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.shmull.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

          var serverletPath = request.getServletPath();

        if (serverletPath.startsWith("/tasks/")) {

          // pegar a autenticação
          var authorization = request.getHeader("Authorization");
        
          var authEncoded = authorization.substring("Basic".length()).trim();
        
        
          // decodificar o base64
          byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
        
          var authDecodedString = new String(authDecoded);

          // separar o usuário da senha
          String[] credentials = authDecodedString.split(":");
          var username = credentials[0];
          var password = credentials[1];


          // validar o usuário
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
              response.sendError(401, "User dont have permission");
              return;
            } else {
              // validar senha
              var passwordHasehed = user.getPassword();
              var passwordValid = BCrypt.verifyer().verify(password.toCharArray(), passwordHasehed);
              if (!passwordValid.verified) {
                response.sendError(401, "User dont have permission");
                return;
              } else {
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
              }
            }
          } else {
            filterChain.doFilter(request, response);
          }

      }
  
}
