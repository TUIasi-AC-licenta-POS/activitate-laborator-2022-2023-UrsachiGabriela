import axios from "axios";

const API_URL = "http://localhost:8082/api/spotify/";


class AuthService {
    login(username, password) {
      return axios.post(API_URL + "login", {
          username,
          password
        })
        .then(response => {
          if (response.data.jwsToken) 
          {
            const token = response.data.jwsToken;
            const headers = { 
                'Authorization': `Bearer ${token}`
            };

            axios.post(API_URL+ "authorize",null,{headers})    
            .then(authResponse => {
                const user = {
                    'sub':`${authResponse.data.sub}`,
                    'roles': `${authResponse.data.roles}`,
                    'token': `${token}`
                }
                localStorage.setItem("user", JSON.stringify(user));
            })
            
          }
  
          return response.data;
        })
        .catch(error =>
            {
                console.log(error);
            });
    }

    logout() {
        const user = JSON.parse(localStorage.getItem('user'));

        const headers = { 
            'Authorization': `Bearer ${user.token}`
        };
        axios.post('http://localhost:8082/api/spotify/login', null,{headers})
        .then(response => {
            // remove user from local storage to log user out
            localStorage.removeItem('token');
            //tokenSubject.next(null);
        })
      }
}

export default AuthService