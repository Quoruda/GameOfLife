# Game of life

![game of life](images/screen.png)

## Description
This is a simple implementation of the Game of Life in Java.
For now, the rule respect the original rule of Conway's Game of Life, 
but it is easily modifiable.

I would like to add a feature to change the rule thanks to the navbar.
## Inputs

You can interact with the simulation with the following inputs:

| Input              | Actions                 |
|--------------------|-------------------------|
| G                  | show/hide grid          |
| Space              | pause/resume simulation |
| Directional arrows | move                    |
| Mouse wheel        | zoom                    |


## Edit
If the simulation is paused, you can change the state of the cells by clicking on them.

![glider](images/glider.gif)

With the `Primordial Soup` button, you can generate a random configuration.
And with the `Clear` button, you can clear the grid.
## Load files

With the `Load File` button, you can load a file with the extension `.lif`.

### Example of file
`pinball.lif`
![pinball](images/pinball.gif)
`wing.lif`
![wing](images/wing.gif)