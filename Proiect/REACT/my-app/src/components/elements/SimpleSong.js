import React from "react"
import dataService from "../../services/data.service";

class SimpleSong extends React.Component{
    constructor(props){
        super(props);


        this.state = {
            id:null,
            name:"" ,
            genre:"",
            year:null,
            type:"",
            artists:[],
            innerSongs:[] ,

            song:null
        };

    }

    componentDidMount(){
        console.log("in simple song: "+this.props.songUrl)
        this.getSongByUrl(this.props.songUrl)
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevProps.songUrl !== this.props.songUrl) {
            this.getSongByUrl(this.props.songUrl)
        }
      }
    // componentDidUpdate(){
    //     console.log("in update")
    //     this.getSongByUrl(this.props.songUrl)
    // }

    getSongByUrl(url){
        dataService.getSongByUrl(url)
        .then((response)=>{
            this.setState({song:response.data})
        })
        .catch((error)=>{
            this.props.handleError(error)
        })
    }
    render(){
        const {song} = this.state;

        return(
            <div>
                {song && (
                    <div>
                        <li>{song.id}</li>
                        <li>{song.name}</li>
                        <li>{song.type}</li>
                        <li>{song.genre}</li>
                        <li>{song.year}</li>
                    </div>

                )}
            
            </div>
         
            
        )
    }
}

export default SimpleSong