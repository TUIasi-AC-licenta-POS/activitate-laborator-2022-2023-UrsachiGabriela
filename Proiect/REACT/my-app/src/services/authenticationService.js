// import { BehaviorSubject } from 'rxjs';
// import axios from 'axios';

// const tokenSubject = new BehaviorSubject(localStorage.getItem('token'));

// const authService = {
//     login,
//     logout,
//     token: tokenSubject.asObservable(),
//     get tokenValue () { return tokenSubject.value }
// };

// function login(name, password) {
//     const body = JSON.stringify({ name, password })

//     axios.post('http://localhost:8082/api/spotify/login', body)
//     .then(response => {
//         //this.setState({ token: response.data.jwsToken });
//         const token = response.data.jwsToken;
//         localStorage.setItem('token',token)
//         tokenSubject.next(token)

//         return token;
//     })
//     .catch(error => {
//         console.error("There was an error: ",error);
//     });
// }

// function logout() {
//     const headers = { 
//         'Authorization': `Bearer ${localStorage.getItem('token')}`
//     };
//     axios.post('http://localhost:8082/api/spotify/login', null,{headers})
//     .then(response => {
//         // remove user from local storage to log user out
//         localStorage.removeItem('token');
//         tokenSubject.next(null);
//     })

// }

// export {authService as authenticationService};
