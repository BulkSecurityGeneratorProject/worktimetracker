# worktimetacker (a JHipster project)

- I started by creating new empty JHipseter application (took cca 15 minutes)
- Then I designed entity model in JDL studio (another 15 minutes)
- I imported entity model to JHipster to create entity
```sh
yo jhipster:import-jdl your-jdl-file.jh
```
- Unfortunately JDL does not support otherEntityField so in every form related entities were searchable by id. Therefor I had to edit entity HTMLs for Angular to show some friendlier attribut like user's login or project's name. (30 minutes)
- I chose imageblob as a format for receipt scans so I changed to support PDF upload. (30 minutes)
- I used Apache POI library to generate Excel file for expenses and added button to Expenses listing where excel can be downloaded. I wanted to add some PowerMock test but haven't managed to get it running, so I got rid of it. (1h 30m)
- And finally, there are statictis for user's total expenses and hours spend working on each project by user (1h 30m)
```sh
GET /api/expenses/total
GET /api/worklogs/total
```

At one time I had to restart my environment and it showed as mistake, because official docker image doesn't seem to be updated and when you want get it up-to-date there are problems with npm creating symlinks on virtual box share and gulp somehow doesn't want to install dependencies automaticaly. It took me a while to overcome this.
