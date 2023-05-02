#!/bin/bash
allow_untranslated=("edu_id_privacy_policy_link" "edu_id_terms_link" "scan_registration_explaination1b" "authorize_login_button" "account_id")

while read line;
do
	if grep -q "$line" ./app/src/main/res/values-nl/strings.xml; then
	    en=`grep "$line" ./app/src/main/res/values/strings.xml`
	    nl=`grep "$line" ./app/src/main/res/values-nl/strings.xml`
	    if [[ "$en" == "$nl" && ! " ${allow_untranslated[*]} " =~ " ${line} " ]]; then
	    	echo "Untranslated in values-nl/strings.xml: $nl"
	    	errors=$((errors+1))
	    fi
	else 
	    	echo "$line not present in the translation file values-nl/strings.xml"
	    	errors=$((errors+1))
	fi
done <<<$(cat ./app/src/main/res/values/strings.xml | awk '/<string name=".*">/{ print $0 }' | awk -F"\"" '{print $2}')
exit ${errors}
