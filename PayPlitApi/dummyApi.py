import firebase_admin
from firebase_admin import credentials, firestore, db
from flask import Flask, request, jsonify
import os

config_path = os.path.join(os.path.dirname(__file__), 'config')
cred = credentials.Certificate(os.path.join(config_path, 'C:\\Users\\aman2\\Desktop\\F\\git_test\\config\\firebase.json'))
firebase_admin.initialize_app(cred,{"databaseURL": "https://kotlinfirebase-95de4-default-rtdb.firebaseio.com/"})

app = Flask(__name__)

'''
groups =>
{
    groupId : String
    groupName : String
    groupMembers : List<String> 
        :memberId : String
}

'''


@app.route("/",methods=["GET"])
def get_group1():
    return "api running ok ",200

@app.route("/groups",methods=["GET"])
def get_groups():
    ref = db.reference("groups")
    groups_data = ref.get()
    groups = []
    if(groups_data != None):
        for group_id,group_info in groups_data.items():
            groups.append({'groupId':group_id,'groupName': group_info['groupName'],'groupMembers':group_info['groupMembers']})
    
    return jsonify(groups),200


@app.route("/groups/<group_id>",methods=["GET","POST"])
def get_group(group_id):
    try:
        return jsonify(db.reference(f"groups").get()[group_id]),200
    except Exception as e:
        return f"Group Error : {e}",404

@app.route("/groups/create",methods = ["POST"])
def create_group():
    data = request.get_json()
    doc_ref = db.reference("groups")
    new_doc_ref = doc_ref.push()
    data["groupId"] = new_doc_ref.key
    new_doc_ref.set(data)
    return "group created",201

@app.route("/groups/<group_id>",methods = ["PUT"])
def add_member_to_group(group_id):
    data = request.get_json()
    doc_ref = db.reference("groups")
    group_data = doc_ref.get()
    try:
        if(group_id in group_data):
            group_data[group_id]['groupMembers'].append(data['memberId'])
            doc_ref.set(group_data)
            return "Member added",200
    except Exception as e:
        return f"Memeber add error : {e}",404
    
@app.route("/groups",methods=["DELETE"])
def delete_group():
    data = request.get_json()
    groupId = data['groupId']
    group_ref = db.reference(f'groups/{groupId}')
    if(group_ref.get() is not None):
        group_ref.delete()
        return "deleted group succ",200
    else:
        return "not found",404       


@app.route("/users")
def get_users():
    ref = db.reference("users")
    users_data = ref.get()
    users = []
    if(users_data != None):
        for user_id, user_info in users_data.items():
            users.append({'user_id': user_id, 'user_name': user_info["user_name"],'user_email': user_info["user_email"]})

    return jsonify(users)
    
    
@app.route("/users/<user_id>",methods=["GET","POST"])
def get_user(user_id):
    user_data = db.reference('users').get()
    try:
        return jsonify(user_data[user_id]),200
    except Exception as e:
                
        print(f"the erorr is {e}")
        return f"the erorr is {e}",404
    

@app.route("/users/create",methods=["POST"])
def create_user():
    data = request.get_json()
    doc_ref =  db.reference('users')
    new_doc_ref = doc_ref.push()
    data["user_id"] = new_doc_ref.key
    new_doc_ref.set(data)
    return "user created",201

@app.route("/users",methods=["DELETE"])
def delete_user():
    data = request.get_json()
    user_id = data['user_id']
    user_ref = db.reference(f'users/{user_id}')
    if(user_ref.get() is not None):
        user_ref.delete()
        return "deleted succ",200
    else:
        return "not found",404
    try:
        if(user_ref.get() is not None):
            user_ref.delete()
        return "deleted succ",200
    except Exception as e:
                
        print(f"the erorr is {e}")
        return f"the erorr is {e}",404
    
if __name__ == "__main__":
    print(__name__)
    app.run("192.168.29.141",debug=True,port=7000)