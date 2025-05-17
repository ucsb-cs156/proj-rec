# docs/dokku.md

This page is not intended as full documentation of dokku setup; for that, please see: 

* <https://ucsb-cs156.github.io/topics/dokku/>

Instead, this is a quick guide to setting up a `proj-rec` 
instance on dokku that assumes you are already familiar with the basic operation of dokku, and just need a "cheat sheet" to 
get up and running quickly.

Throughout, we use `rec` as the appname.  Substitute any other appropriate name, e.g. `rec-qa`, `rec-dev-cgaucho`, `rec-pr235` as needed.

The lines in the instructions where you need to modify something are marked with the comment: `# modify this`

* For the values of `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` see [docs/oauth.md](https://github.com/ucsb-cs156/proj-rec/blob/main/docs/oauth.md)
* For the value of `UCSB_API_KEY` see: [UCSB Developer API overview](https://ucsb-cs156.github.io/topics/apis/apis_ucsb_developer_api.html)
* Set `SOURCE_REPO` to be the url of your teams' repo (i.e. replace the team name in the example below) 

The other line you can copy/paste as is, except for changing `rec` to whatever your app name will be (e.g. `rec-qa`, `rec-dev-cgaucho`, `rec-pr235`).

```
# Create app
dokku apps:create rec

# Create and link postgres database
dokku postgres:create rec-db
dokku postgres:link rec-db rec --no-restart

# Modify dokku settings
dokku git:set rec keep-git-dir true

# Set config vars
dokku config:set --no-restart rec PRODUCTION=true
dokku config:set --no-restart rec GOOGLE_CLIENT_ID=get-value-from-google-developer-console # modify this
dokku config:set --no-restart rec GOOGLE_CLIENT_SECRET=get-value-from-google-developer-console # modify this

# Set SOURCE_REPO to your repo (modify the url)
# This is for the link in the footer, and for the link to currently deployed branch in /api/systemInfo
dokku config:set --no-restart rec SOURCE_REPO=https://github.com/ucsb-cs156-s25/proj-rec-s25-xx 

# Set ADMIN_EMAILS to staff emails and team emails
dokku config:set --no-restart rec ADMIN_EMAILS=list-of-admin-emails # modify this

# git sync for first deploy (http)
dokku git:sync rec https://github.com/ucsb-cs156-s25/proj-rec-s25-xx main  # modify this 
dokku ps:rebuild rec

# Enable https
dokku letsencrypt:set rec email yourEmail@ucsb.edu # modify email
dokku letsencrypt:enable rec
```

