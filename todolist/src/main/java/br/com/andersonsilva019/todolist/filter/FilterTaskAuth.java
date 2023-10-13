package br.com.andersonsilva019.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.andersonsilva019.todolist.users.IUserRepository;
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

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks")) {
            var authorization = request.getHeader("Authorization");

            if (authorization == null || authorization.isEmpty() || !authorization.startsWith("Basic")) {
                response.setStatus(401);
                return;
            }

            String userPassword = authorization.substring("Basic".length()).trim();

            String decoded = new String(Base64.getDecoder().decode(userPassword));

            String[] userAndPass = decoded.split(":");

            String username = userAndPass[0];
            String password = userAndPass[1];

            var user = this.userRepository.findByUsername(username);

            if (user == null) {
                response.sendError(401);
            } else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if (passwordVerify.verified) {
                    request.setAttribute("userId", user.getId());

                    filterChain.doFilter(request, response);
                }else {
                    response.sendError(401);
                }

            }
        }else {
            filterChain.doFilter(request, response);
        }

    }

}
